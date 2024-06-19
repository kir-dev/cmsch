import { Alert, AlertDescription, AlertIcon, Box, Button, HStack, useDisclosure, VStack } from '@chakra-ui/react'
import { useConfigContext } from '../api/contexts/config/ConfigContext.tsx'
import { areNotificationsSupported } from '../util/configs/firebase.config.ts'

export const EnableNotifications = () => {
  const { isOpen, onClose } = useDisclosure({ defaultIsOpen: true })
  const config = useConfigContext()
  if (!areNotificationsSupported()) return
  if (Notification.permission !== 'default') return null // we cannot ask again

  const component = config.components.pushnotification
  if (!component?.notificationsEnabled || !isOpen || !shouldShowAlert(component.permissionAllowNeverShowAgain)) return null

  const { permissionPromptText, permissionAcceptText, permissionDenyText } = component
  return (
    <Box>
      <Alert
        borderRadius={[0, null, 'xl']}
        opacity={1}
        status={'info'}
        variant="solid"
        mx="auto"
        w="100%"
        maxWidth={['100%', '64rem']}
        mb={5}
      >
        <HStack justify="space-between" flex={1}>
          <AlertIcon display={['none', 'none', 'block']} />
          <VStack align="flex-start" flex={1}>
            <AlertDescription wordBreak="break-word">{permissionPromptText}</AlertDescription>
          </VStack>
          <Button variant="ghost" textColor="000" mr={2} onClick={() => enableNotifications(onClose, () => window.location.reload())}>
            {permissionAcceptText || 'Igen'}
          </Button>
          {!!permissionDenyText && (
            <Button variant="ghost" textColor="000" mr={2} onClick={() => disableNotifications(onClose)}>
              {permissionDenyText}
            </Button>
          )}
        </HStack>
      </Alert>
    </Box>
  )
}

function shouldShowAlert(allowedToNeverShowAgain: boolean | undefined) {
  return !allowedToNeverShowAgain || localStorage.getItem('show-notification-alert') !== 'false'
}

function disableAlert() {
  localStorage.setItem('show-notification-alert', 'false')
}

async function enableNotifications(onClose: () => void, onEnabled: () => void) {
  onClose()
  const result = await Notification.requestPermission()
  if (result === 'granted') {
    onEnabled()
  }
}

function disableNotifications(onClose: () => void) {
  onClose()
  disableAlert()
}
