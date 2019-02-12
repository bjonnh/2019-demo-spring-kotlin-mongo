package net.nprod.demo

import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.beans.factory.annotation.Autowired
import net.nprod.demo.component.SecUserDetailsService
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import javax.servlet.http.HttpServletResponse
import java.io.IOException
import javax.servlet.http.HttpServletRequest
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component
import org.springframework.security.web.savedrequest.RequestCache
import javax.servlet.ServletException
import org.springframework.security.web.savedrequest.HttpSessionRequestCache
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler
import org.springframework.util.StringUtils
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler

/*
 * The basic class for REST authentication
 */
@Component
class RestAuthenticationEntryPoint : AuthenticationEntryPoint {

    @Throws(IOException::class)
    override fun commence(
            request: HttpServletRequest,
            response: HttpServletResponse,
            authException: AuthenticationException) {

        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized")
    }

}

/*
 * This is the component that keep the auth request cached
 */

@Component
class MySavedRequestAwareAuthenticationSuccessHandler : SimpleUrlAuthenticationSuccessHandler() {

    private var requestCache: RequestCache =
            HttpSessionRequestCache()

    @Throws(ServletException::class, IOException::class)
    override fun onAuthenticationSuccess(request: HttpServletRequest, response: HttpServletResponse, authentication: Authentication) {
        val savedRequest =
                requestCache.getRequest(request, response)

        if (savedRequest == null) {
            clearAuthenticationAttributes(request)
            return
        }
        val targetUrlParameter =
                targetUrlParameter
        if (isAlwaysUseDefaultTargetUrl || targetUrlParameter != null && StringUtils.hasText(request.getParameter(targetUrlParameter))) {
            requestCache.removeRequest(request, response)
            clearAuthenticationAttributes(request)
            return
        }

        clearAuthenticationAttributes(request)
    }

    fun setRequestCache(requestCache: RequestCache) {
        this.requestCache =
                requestCache
    }
}

/*
 * This is configuration for the security aspect. This is where we can limit access globally to some endpoints
 * But access can also be more fine-grained at the level of the controllers, see controller/SpectraController.kt
 */

@Configuration
@EnableWebSecurity
class SecurityConfig : WebSecurityConfigurerAdapter() {

    @Autowired
    private val restAuthenticationEntryPoint: RestAuthenticationEntryPoint? =
            null

    @Autowired
    private val mySuccessHandler: MySavedRequestAwareAuthenticationSuccessHandler? =
            null

    private val myFailureHandler =
            SimpleUrlAuthenticationFailureHandler()

    @Autowired
    internal var userDetailsService: SecUserDetailsService? =
            null

    @Autowired
    @Throws(Exception::class)
    fun configAuthBuilder(builder: AuthenticationManagerBuilder) {
        builder.userDetailsService<SecUserDetailsService>(userDetailsService)

    }

    @Throws(Exception::class)
    override fun configure(http: HttpSecurity) {
        http
                .csrf().disable()
                .exceptionHandling()
                .authenticationEntryPoint(restAuthenticationEntryPoint)
                .and()
                .authorizeRequests()
                .antMatchers("/spectra/**").permitAll()
                .antMatchers("/people/**").hasRole("ADMIN")
                .antMatchers("/**").permitAll()
                .and()
                .formLogin()
                .successHandler(mySuccessHandler)
                .failureHandler(myFailureHandler)
                .and()
                .logout()
    }
}