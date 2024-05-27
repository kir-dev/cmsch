package hu.bme.sch.cmsch.component.pushnotification

import jakarta.persistence.*
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean

@Entity
@Table(
    name = "messaging_tokens",
    indexes = [Index(columnList = "userId"), Index(columnList = "userId,token", unique = true)]
)
@ConditionalOnBean(PushNotificationComponent::class)
class MessagingTokenEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    open var id: Long? = null,

    @Column(name = "userId", nullable = false)
    open var userId: Int = 0,

    @Column(name = "token", nullable = false)
    open var token: String = ""
)
