package hu.bme.sch.cmsch.config

import com.fasterxml.jackson.annotation.JsonInclude
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.TypeDescriptor
import org.springframework.core.convert.converter.GenericConverter
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator
import org.springframework.security.oauth2.core.OAuth2TokenValidator
import org.springframework.security.oauth2.core.converter.ClaimConversionService
import org.springframework.security.oauth2.jwt.*
import tools.jackson.databind.ObjectMapper
import tools.jackson.databind.json.JsonMapper
import tools.jackson.module.kotlin.kotlinModule
import java.net.URI
import java.net.URL
import java.util.*

@Configuration
@EnableScheduling
@EnableAsync
class AppConfig {

    @Bean
    fun objectMapper(): ObjectMapper {
        return JsonMapper.builder()
            .addModule(kotlinModule { })
            .changeDefaultPropertyInclusion { it.withValueInclusion(JsonInclude.Include.NON_NULL) }
            .build()
    }

    @Bean
    fun jwtDecoder(): JwtDecoder {

        ClaimConversionService.getSharedInstance().addConverter(object : GenericConverter {
            override fun convert(source: Any?, sourceType: TypeDescriptor, targetType: TypeDescriptor): Any? {
                if (source == null) {
                    return null
                }
                if (source is URL) {
                    return source
                }
                try {
                    return URI("https://$source").toURL()
                } catch (_: Exception) {
                    // Ignore
                }
                return null
            }

            override fun getConvertibleTypes(): MutableSet<GenericConverter.ConvertiblePair>? {
                return Collections.singleton(GenericConverter.ConvertiblePair(Any::class.java, URL::class.java))
            }
        })

        val jwtDecoder = JwtDecoders.fromIssuerLocation("https://accounts.google.com") as NimbusJwtDecoder
        val withIssuer = JwtValidators.createDefaultWithIssuer("accounts.google.com")
        val withAudience: OAuth2TokenValidator<Jwt> = DelegatingOAuth2TokenValidator(withIssuer)
        jwtDecoder.setJwtValidator(withAudience)
        return jwtDecoder
    }

}
