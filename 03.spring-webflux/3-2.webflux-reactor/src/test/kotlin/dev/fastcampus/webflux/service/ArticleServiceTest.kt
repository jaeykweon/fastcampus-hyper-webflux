package dev.fastcampus.webflux.service

import dev.fastcampus.webflux.exception.NotFoundException
import mu.KotlinLogging
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import reactor.core.publisher.Mono

private val logger = KotlinLogging.logger{}

@SpringBootTest
@ActiveProfiles("test")
class ArticleServiceTest(
    @Autowired private val articleService: ArticleService,
) {

//    @Test
//    fun getAll() {
//
//        articleService.create(ReqCreate("title 1", "blabla 01", 1234)).block()
//        articleService.create(ReqCreate("title 2", "blabla 02", 1234)).block()
//        articleService.create(ReqCreate("title 3", "blabla 03", 1234)).block()
//
//        articleService.getAll().collectList().doOnNext { assertEquals(3, it.size) }.block()
//        articleService.getAll("2").collectList().doOnNext { assertEquals(1, it.size) }.block()
//    }

    @Test
    fun getAll() {
        Mono.zip(
            articleService.create(ReqCreate("title 1", "blabla 01", 1234)),
            articleService.create(ReqCreate("title 2", "blabla 02", 1234)),
            articleService.create(ReqCreate("title 3", "blabla 03", 1234)),
        ).flatMap {
            articleService.getAll().collectList().doOnNext {
                assertEquals(3, it.size)
            }
        }.flatMap {
            articleService.getAll("2").collectList().doOnNext { assertEquals(1, it.size) }
//        }.block()
        }.rollback().block()
    }

    @Test
    fun get() {
        val new = articleService.create(ReqCreate("title 1", "blabla 01", 1234)).block()!!
        articleService.get(new.id).doOnNext {
            assertEquals("title 1", it.title)
            assertEquals("blabla 01", it.body)
            assertEquals(1234, it.authorId)
        }.rollback().block()
//        }.block()
        assertThrows<NotFoundException> {
            articleService.get(-1).block()
        }
    }

    @Test
    fun create() {
        val request = ReqCreate("title 4", "blabla 04", 1234)
        articleService.create(request).doOnNext {
            assertEquals(request.title, it.title)
            assertEquals(request.body, it.body)
            assertEquals(request.authorId, it.authorId)
        }.rollback().block()
//        }.block()
    }

    @Test
    @Disabled
    fun delete() {
        val prevSize = getArticleSize()
        val new = articleService.create(ReqCreate("title 4", "blabla 04", 1234)).toFuture().get()
        assertEquals(prevSize + 1, getArticleSize())
        articleService.delete(new.id).block()
        assertEquals(prevSize, getArticleSize())
    }

    private fun getArticleSize(): Int = articleService.getAll().collectList().map { it.size }.block() ?: 0

    @Test
    fun deleteInRollback() {
        articleService.getAll().collectList().map { it.size }.flatMap { prevSize ->
            articleService.create(ReqCreate("title 4", "blabla 04", 1234)).flatMap { new ->
                articleService.getAll().collectList().map { it.size }.flatMap {
                    assertEquals(prevSize + 1, it)
                    articleService.delete(new.id).thenReturn(true).flatMap {
                        articleService.getAll().collectList().map { it.size }.doOnNext {
                            assertEquals(prevSize, it)
                        }
                    }
                }
            }
        }.rollback().block()
//        }.block()
    }

    @Test
    fun deleteInRollbackInFunctional() {
        articleService.getAll().collectList().map { it.size }.flatMap { prevSize ->
            articleService.create(ReqCreate("title 4", "blabla 04", 1234))
                .zipWhen { articleService.getAll().collectList().map { it.size } }
                .flatMap { Mono.zip(Mono.just(prevSize), Mono.just(it.t1), Mono.just(it.t2)) }
        }.flatMap {
            val (prevSize, created, currSize) = Triple(it.t1, it.t2, it.t3)
            assertEquals(prevSize + 1, currSize)
            articleService.delete(created.id).thenReturn(true)
                .zipWhen { articleService.getAll().collectList().map { it.size } }
                .flatMap { Mono.zip(Mono.just(prevSize), Mono.just(it.t2)) }
        }.flatMap {
            val (prevSize, currSize) = it.t1 to it.t2
            assertEquals(prevSize, currSize)
            Mono.just(true)
        }.rollback().block()
//        }.block()
    }

}