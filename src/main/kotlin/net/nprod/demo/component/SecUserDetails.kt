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
import java.util.ArrayList
import net.nprod.demo.model.Privilege

/*
 * An example of a custom user details service that grant modular privileges according to the role of the user
 */


@Component
class SecUserDetailsService : UserDetailsService {

    @Autowired
    private val userRepository: UserRepository? =
            null

    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(username: String): UserDetails {
        /*Here add user data layer fetching from the MongoDB.
                 I have used userRepository*/
        val user =
                userRepository!!.findByLogin(username)
        if (user == null) {
            throw UsernameNotFoundException(username)
        } else {
            return SecUserDetails(user)
        }
    }
}

class SecUserDetails(val user: User): UserDetails {

    override fun getAuthorities(): Collection<GrantedAuthority> {
        return getGrantedAuthorities(getPrivileges(user.roles))
    }

    private fun getPrivileges(roles: Collection<Role>): List<String> {

        val privileges =
                ArrayList<String>()
        val collection =
                ArrayList<Privilege>()
        for (role in roles) {
            collection.addAll(role.privileges)
        }
        for (item in collection) {
            privileges.add(item.name)
        }
        return privileges
    }

    private fun getGrantedAuthorities(privileges: List<String>): List<GrantedAuthority> {
        val authorities =
                ArrayList<GrantedAuthority>()
        for (privilege in privileges) {
            authorities.add(SimpleGrantedAuthority(privilege))
        }
        return authorities
    }

    override fun isEnabled(): Boolean {
        return true
    }

    override fun getUsername(): String = user.login

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun getPassword(): String {
        return user.password ?: ""  // If no password, it will act as a deactivated account
    }

    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        return true
    }
}