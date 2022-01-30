import { Alert, AlertIcon } from '@chakra-ui/react'

type PresenceAlertProps = {
  acquired: number
  needed: number
  mt?: number
}

export const PresenceAlert: React.FC<PresenceAlertProps> = ({ acquired, needed, mt = 5 }) => {
  if (acquired == null || needed == null) return null
  else if (acquired < needed)
    return (
      <Alert variant="left-accent" status="info" mt={mt}>
        <AlertIcon />
        Még {needed - acquired} darab QR kód kell a tanköri jelenlét megszerzéséig.
      </Alert>
    )
  else
    return (
      <Alert variant="left-accent" status="success" mt={mt}>
        <AlertIcon />
        Megvan a tanköri jelenlét!
      </Alert>
    )
}
