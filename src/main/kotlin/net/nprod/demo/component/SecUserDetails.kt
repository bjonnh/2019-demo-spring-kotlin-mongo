package net.nprod.demo.component

import net.nprod.demo.model.Role
import net.nprod.demo.model.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import net.nprod.demo.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Component
import org.springframework.security.core.authority.SimpleGrantedAuthority

/*
 * An example of a custom user details service that grant modular privileges according to the role of the user
 */


@Component
class SecUserDetailsService : UserDetailsService {

    @Autowired
    private lateinit var userRepository: UserRepository

    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(username: String): UserDetails {
        /*Here add user data layer fetching from the MongoDB.
                 I have used userRepository*/
        val user =
                userRepository.findByLogin(username)

        if (user == null) {
            throw UsernameNotFoundException(username)
        } else {
            return SecUserDetails(user)
        }
    }
}

class SecUserDetails(private val user: User) : UserDetails {
    /*
     * Get all the privileges that this user has
     */

    override fun getAuthorities(): Collection<GrantedAuthority> =
            user.roles.flatMap { role ->
                role.privileges.map { privilege ->
                    SimpleGrantedAuthority(privilege.name) } }

    override fun isEnabled(): Boolean = true

    override fun getUsername(): String = user.login

    override fun isCredentialsNonExpired() = true

    override fun getPassword() = user.password ?: ""  // If no password, it will act as a deactivated account

    override fun isAccountNonExpired() = true

    override fun isAccountNonLocked() = true
}