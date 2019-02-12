package net.nprod.demo.controller


import net.nprod.demo.component.SecUserDetailsService
import net.nprod.demo.model.Spectrum
import net.nprod.demo.repository.SpectrumRepository
import net.nprod.demo.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.security.Principal

@RestController
@PreAuthorize("permitAll")
@RequestMapping("/spectra")
class SpectraController {
    @Autowired
    internal lateinit var userDetailsService: SecUserDetailsService

    @Autowired
    private lateinit var repository: SpectrumRepository

    @Autowired
    private lateinit var userRepository: UserRepository

    @PreAuthorize("permitAll")
    @RequestMapping("/{id}", method = [RequestMethod.GET])
    fun getSpectrumById(@PathVariable id: String, user: Principal?): Spectrum {
        try {
            val spect =
                    repository.findById(id)
            return if (user != null) { // Are we logged in?
                val authUser =
                        userDetailsService.loadUserByUsername(user.name)

                spect.filter {spectrum ->
                    (spectrum.owner == authUser.username) or  // It is owned by this user
                            spectrum.public or // or public
                            (authUser.authorities.any { it.authority == "ROLE_ADMIN" }) // or user is an admin
                }.get()
            } else { // If we are not logged in, any public spectrum can be displayed
                spect.filter {
                    it.public
                }.get()
            }
        } catch (ex: java.util.NoSuchElementException) {
            throw ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Spectrum Not Found", ex)
        }
    }
}
