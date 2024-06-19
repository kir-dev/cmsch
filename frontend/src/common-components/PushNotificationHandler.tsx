import { FC, PropsWithChildren, useEffect } from 'react'
import { areNotificationsSupported, disableNotifications, getCloudMessaging, initNotifications } from '../util/configs/firebase.config.ts'
import { useConfigContext } from '../api/contexts/config/ConfigContext.tsx'
import { Alert, AlertDescription, AlertTitle, Box, Button, CloseButton, useToast } from '@chakra-ui/react'
import { useAuthContext } from '../api/contexts/auth/useAuthContext.ts'
import { MessagePayload } from '@firebase/messaging'
import { ExternalLinkIcon } from '@chakra-ui/icons'

export const PushNotificationHandler: FC<PropsWithChildren> = ({ children }) => {
  const authContext = useAuthContext()
  const config = useConfigContext()
  const toast = useToast()

  useEffect(() => {
    if (!areNotificationsSupported()) return
    if (!authContext.isLoggedIn) return
    const shouldShowNotifications = config.components.pushnotification?.notificationsEnabled
    const permissionGranted = Notification.permission === 'granted'
    if (shouldShowNotifications && permissionGranted) {
      const messaging = getCloudMessaging()
      if (messaging === null) return
      initNotifications(messaging, (payload) =>
        toast({
          render: (options) => <NotificationToast payload={payload} onClose={options.onClose} />,
          duration: 5000,
          isClosable: true
        })
      )
    } else {
      disableNotifications()
    }
  }, [authContext.isLoggedIn, config.components.pushnotification?.notificationsEnabled, Notification?.permission])
  return <>{children}</>
}

function NotificationToast({ payload, onClose }: { payload: MessagePayload; onClose?: () => void }) {
  const { body, image, title } = payload.notification ?? {}
  const link = payload.fcmOptions?.link
  return (
    <Alert status="info" variant="solid" flexDirection="column" textAlign="center" borderRadius={[0, null, 'xl']} position="relative">
      <CloseButton onClick={onClose} position="absolute" top={4} right={4} />
      {image && <img width={120} height={120} src={image} alt={title} />}
      {title && (
        <AlertTitle mt={4} mb={1} fontSize="lg">
          {title}
        </AlertTitle>
      )}
      {body && <AlertDescription maxWidth="sm">{body}</AlertDescription>}
      {link && (
        <Button as={'a'} variant="ghost" textColor="000" href={link} mt={4} target="_blank">
          <Box pr={2}>Megnyitás</Box> <ExternalLinkIcon />
        </Button>
      )}
    </Alert>
  )
}
