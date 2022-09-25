package hu.bme.sch.cmsch.component.bmejegy

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.ExchangeFilterFunction
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import reactor.netty.http.client.HttpClient
import javax.annotation.PostConstruct

const val USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/106.0.0.0 Safari/537.36"

@Service
@ConditionalOnBean(BmejegyComponent::class)
open class BmejegyService(
    @Value("\${hu.bme.sch.cmsch.component.bmejegy.bmejegyservice.username:}") private val bmejegyUsername: String,
    @Value("\${hu.bme.sch.cmsch.component.bmejegy.bmejegyservice.password:}") private val bmejegyPassword: String,
    private val bmejegy: BmejegyComponent
) {

    fun logRequest(): ExchangeFilterFunction {
        return ExchangeFilterFunction.ofRequestProcessor({ clientRequest ->
            val sb = StringBuilder("Request: \n")
            clientRequest
                .headers()
                .forEach({ name, values -> sb.append("h ").append(name).append("=").append(values).append("\n")})
            clientRequest
                .cookies()
                .forEach({ name, values -> sb.append("c ").append(name).append("=").append(values).append("\n")})

            println(sb.toString())
            return@ofRequestProcessor Mono.just(clientRequest)
        })
    }

    @Async
    open fun fetchData() {
        println("fetchData()")
        val client = WebClient.builder()
            .baseUrl("https://www.bmejegy.hu")
            .clientConnector(
                ReactorClientHttpConnector(HttpClient.create()
                    .followRedirect(true)
                    .keepAlive(true)
                    .wiretap(true)
                )
            )
            .filters {
                it.add(logRequest())
            }
            .build()

        val responseLogin1 = client.get()
            .uri("/fiokom/")
            .header(HttpHeaders.USER_AGENT, USER_AGENT)
            .accept(MediaType.TEXT_HTML, MediaType.APPLICATION_XHTML_XML)
            .retrieve()
            .toEntity(String::class.java)
            .block()!!

        val nonce = extractNonceFromResponse(responseLogin1)
        println("Nonce $nonce")

        val login = client.post()
            .uri("/fiokom/")
            .header(HttpHeaders.USER_AGENT, USER_AGENT)
            .accept(MediaType.TEXT_HTML, MediaType.APPLICATION_XHTML_XML)
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
            .body(
                BodyInserters.fromFormData("username", bmejegyUsername)
                    .with("password", bmejegyPassword)
                    .with("login", "Bejelentkezés")
                    .with("_wp_http_referer", "/fiokom/")
                    .with("woocommerce-login-nonce", nonce)
            )
            .retrieve()
            .toEntity(String::class.java)
            .block()!!

        println("Login ${login.statusCode}")

        val sessionCookieWpAdmin = login.headers["Set-Cookie"]?.get(1)?.split("=", ";")
            ?: throw IllegalStateException("Invalid cookie")
        val sessionCookieSite = login.headers["Set-Cookie"]?.get(2)?.split("=", ";")
            ?: throw IllegalStateException("Invalid cookie")

        val responseLogin2 = client.get()
            .uri("/fiokom/")
            .header(HttpHeaders.USER_AGENT, USER_AGENT)
            .accept(MediaType.TEXT_HTML, MediaType.APPLICATION_XHTML_XML)
            .retrieve()
            .toEntity(String::class.java)
            .block()!!

        println("Account ${responseLogin2.statusCode}")

        val reportResponse = client.get()
            .uri("/rendeles-riportok/")
            .header(HttpHeaders.USER_AGENT, USER_AGENT)
            .accept(MediaType.TEXT_HTML, MediaType.APPLICATION_XHTML_XML)
//            .header(HttpHeaders.COOKIE, sessionCookieSite[0] + "=" + sessionCookieSite[1])
            .cookie(sessionCookieSite[0], sessionCookieSite[1])
//            .header("Cookie", sessionCookieWpAdmin[0] + "=" + sessionCookieWpAdmin[1] + "; "
//                    + sessionCookieSite[0] + "=" + sessionCookieSite[1] + "; " +
//                    "euCookie=set; _icl_current_language=hu")
            .retrieve()
            .toEntity(String::class.java)
            .block()!!

        println("Reports GET ${reportResponse.statusCode}")

        val ajaxResponse = client.post()
            .uri {
                it.path("/wp-admin/admin-ajax.php")
                    .queryParam("action", "onliner_ajax")
                    .queryParam("onliner_ajax_action", "jqgrid_order_result")
                    .build()
            }
            .header(HttpHeaders.USER_AGENT, USER_AGENT)
            .accept(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
            .header("Sec-Fetch-Dest", "empty")
            .header("Sec-Fetch-Mode", "cors")
            .header("Sec-Fetch-Site", "same-origin")
            .header("Host", "www.bmejegy.hu")
            .header("Origin", "https://www.bmejegy.hu")
            .header("Referer", "https://www.bmejegy.hu/rendeles-riportok/")
            .header("Cookie", sessionCookieWpAdmin[0] + "=" + sessionCookieWpAdmin[1] + "; "
                    + sessionCookieSite[0] + "=" + sessionCookieSite[1] + "; " +
                    "euCookie=set; _icl_current_language=hu")
            .body(
                BodyInserters.fromFormData("_search", "false")
                    .with("nd", "1666878881355") // bmejegy.minTimestamp.getValue()
                    .with("rows", "25") // 100000
                    .with("page", "1")
                    .with("sidx", "")
                    .with("sord", "asc")
            )
            .retrieve()
            .toEntity(String::class.java)
            .block()!!

        println("---")

        println(ajaxResponse.body)

    }

    private fun extractNonceFromResponse(response: ResponseEntity<String>): String {
        val body = response.body ?: ""
        val pattern = "name=\"woocommerce-login-nonce\" value=\""
        val indexOf = body.indexOf(pattern).coerceAtLeast(0)
        return body.substring(indexOf + pattern.length, body.indexOf("\"", indexOf + pattern.length + 1))
    }

}
