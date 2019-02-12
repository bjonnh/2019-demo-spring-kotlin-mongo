package net.nprod.demo

import com.mongodb.MongoClient
import com.mongodb.MongoClientOptions
import net.nprod.demo.model.User
import net.nprod.demo.repository.UserRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import com.mongodb.MongoCredential
import com.mongodb.ServerAddress
import net.nprod.demo.model.Privilege
import net.nprod.demo.model.Role
import net.nprod.demo.model.Spectrum
import net.nprod.demo.repository.SpectrumRepository
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.config.AbstractMongoConfiguration
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories
import org.springframework.security.crypto.factory.PasswordEncoderFactories


@Configuration
class ApplicationContextEventTestsAppConfig : AbstractMongoConfiguration() {

    public override fun getDatabaseName(): String {
        return "database"
    }

    @Bean
    override fun mongoClient(): MongoClient {
        return MongoClient(ServerAddress("mongo", 27017),
                MongoCredential.createCredential("root", "admin", "example".toCharArray()),
                MongoClientOptions.Builder().build())
    }
}

@EnableMongoRepositories(basePackages = ["net.nprod.demo.repository"])
@SpringBootApplication
class Application {
    @Bean
    fun init(userRepository: UserRepository, spectrumRepository: SpectrumRepository) = CommandLineRunner {

        /*
         * Here we create initial data for the demo
         * A simple user, and an administrator, each having two spectrum, one public and one private
         */
        val encoder =
                PasswordEncoderFactories.createDelegatingPasswordEncoder()
        val adminRole = Role("Administrator", "Administrator",
                listOf(Privilege("ROLE_ADMIN", "ROLE_ADMIN")))

        val userRole = Role("User", "Simple user", // we create an admin role
                listOf(Privilege("ROLE_USER", "ROLE_USER")))

        val admin = User("admin", "admin", "Foo", "Bar",
                encoder.encode("admin"), // We encrypt the password with the default encoder (bcrypt IIRC)
                listOf(adminRole))
        userRepository.save(admin)  // that can do anything

        val user = User("user", "user", "Foo", "Bar",
                encoder.encode("user"), // We encrypt the password with the default encoder (bcrypt IIRC)
                listOf(userRole))
        userRepository.save(user)  // that can do anything

        spectrumRepository.save(Spectrum("AS1", "Administrator private spectrum", false, admin.id))
        spectrumRepository.save(Spectrum("AS2", "Administrator public spectrum", true, admin.id))
        spectrumRepository.save(Spectrum("US1", "User private spectrum", false, user.id))
        spectrumRepository.save(Spectrum("US2", "User public spectrum", true, user.id))
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            SpringApplication.run(Application::class.java, *args)
        }
    }
}
