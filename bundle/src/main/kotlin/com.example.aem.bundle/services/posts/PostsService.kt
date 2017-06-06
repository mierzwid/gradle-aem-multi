package com.example.aem.bundle.services.posts

import com.example.aem.bundle.common.JsonUtils
import com.google.gson.reflect.TypeToken
import org.osgi.service.component.annotations.Activate
import org.osgi.service.component.annotations.Component
import org.osgi.service.metatype.annotations.AttributeDefinition
import org.osgi.service.metatype.annotations.Designate
import org.osgi.service.metatype.annotations.ObjectClassDefinition
import java.util.*
import java.net.URL as Url

@Component(
        service = arrayOf(PostsService::class),
        immediate = true
)
@Designate(ocd = PostsService.Config::class)
class PostsService {

    private lateinit var config: Config

    @Activate
    private fun activate(config: Config) {
        this.config = config
    }

    val posts: List<Post> by lazy {
        JsonUtils.GSON.fromJson<List<Post>>(
                // TODO url is null
                Url(config.url).openStream().bufferedReader().use { it.readText() },
                object : TypeToken<List<Post>>() {}.type
        )
    }

    fun randomPosts(count: Int): List<Post> {
        val shuffled = posts.toMutableList()
        Collections.shuffle(shuffled)

        return shuffled.subList(0, count)
    }

    @ObjectClassDefinition(
            name = "Posts Service Config",
            description = "Customize endpoint URL for service which is returning posts"
    )
    annotation class Config(
            @get:AttributeDefinition(
                    name = "Data source URL for posts.",
                    description = "Endpoint must return JSON with list of objects containing 'userId', 'id', 'title' and 'body'.",
                    defaultValue = arrayOf("https://jsonplaceholder.typicode.com/posts")
            ) val url: String
    )


}