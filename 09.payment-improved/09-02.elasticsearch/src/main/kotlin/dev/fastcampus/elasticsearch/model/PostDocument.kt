package dev.fastcampus.elasticsearch.model

import org.springframework.data.annotation.Id
import org.springframework.data.elasticsearch.annotations.DateFormat
import org.springframework.data.elasticsearch.annotations.Document
import org.springframework.data.elasticsearch.annotations.Field
import org.springframework.data.elasticsearch.annotations.FieldType
import java.time.LocalDateTime

@Document(indexName = "post")
class PostDocument {

    @Id
    var id: Long = 0

    var title: String? = null

    var body: String? = null

    var authorId: Long? = null

    @Field(type = FieldType.Date, format = [DateFormat.date_hour_minute_second_millis])
    var createdAt: LocalDateTime? = LocalDateTime.now()

    @Field(type = FieldType.Date, format = [DateFormat.date_hour_minute_second_millis])
    var updatedAt: LocalDateTime? = LocalDateTime.now()

    constructor()
    constructor(
        id: Long,
        title: String?,
        body: String?,
        authorId: Long?,
        createdAt: LocalDateTime? = LocalDateTime.now(),
        updatedAt: LocalDateTime? = LocalDateTime.now(),
    ) {
        this.id = id
        this.title = title
        this.body = body
        this.authorId = authorId
        this.createdAt = createdAt
        this.updatedAt = updatedAt
    }

}