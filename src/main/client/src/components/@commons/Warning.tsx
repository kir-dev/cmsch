import * as React from 'react'
import { Alert, AlertDescription, AlertIcon, AlertTitle, CloseButton } from '@chakra-ui/react'
import axios from 'axios'
import { API_BASE_URL } from '../../utils/configurations'
import { WarningView } from 'types/dto/warning'
import { Container } from 'components/@layout/Container'

export const Warning: React.FC = () => {
  const [warning, setWarning] = React.useState<WarningView | undefined>(undefined)
  const [closed, setClosed] = React.useState<boolean>(false)

  const closeWarning = () => setClosed(true)

  React.useEffect(() => {
    axios.get<WarningView>(`${API_BASE_URL}/api/warning`).then((res) => {
      if (res.data.message !== '') setWarning(res.data)
    })
  }, [setWarning])

  return warning === undefined || closed ? (
    <></>
  ) : (
    <Container>
      <Alert status="warning" variant="left-accent">
        <AlertIcon />
        <AlertTitle mr={2}>{warning.type}</AlertTitle>
        <AlertDescription>{warning.message}</AlertDescription>
        <CloseButton position="absolute" right="8px" top="8px" onClick={closeWarning} />
      </Alert>
    </Container>
  )
}
