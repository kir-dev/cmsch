import { Alert, AlertIcon } from '@chakra-ui/react'
import { FC } from 'react'
import { useConfigContext } from '../api/contexts/config/ConfigContext'

type PresenceAlertProps = {
  acquired: number
  needed: number
  mt?: number
}

export const PresenceAlert: FC<PresenceAlertProps> = ({ acquired, needed, mt = 5 }) => {
  const config = useConfigContext()
  const component = config?.components.profile
  if (acquired == null || needed == null) return null
  else if (acquired < needed)
    return (
      <Alert variant="left-accent" status="info" mt={mt}>
        <AlertIcon />
        {component?.minTokenMsg}
      </Alert>
    )
  else
    return (
      <Alert variant="left-accent" status="success" mt={mt}>
        <AlertIcon />
        {component?.minTokenAchievedMsg}
      </Alert>
    )
}
