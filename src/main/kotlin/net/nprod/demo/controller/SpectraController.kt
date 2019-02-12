package net.nprod.demo.controller


import net.nprod.demo.model.Spectrum
import net.nprod.demo.repository.SpectrumRepository
import net.nprod.demo.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.security.Principal


@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "spectrum not found")
class SpectrumNotFoundException : RuntimeException()

@RestController
@PreAuthorize("permitAll")
@RequestMapping("/spectra")
class SpectraController {
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
                        userRepository.findByLogin(user.name)
                spect.filter {
                    (it.owner == authUser?.id) or  // It is owned by this user
                            it.public or // or public
                            ((authUser?.roles?.count { role -> role.name == "ROLE_ADMIN" }) ?: 0 > 0)
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
