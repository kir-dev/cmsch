import * as React from 'react'
import { Alert, AlertDescription, AlertIcon, AlertTitle, CloseButton, HStack, VStack } from '@chakra-ui/react'
import axios from 'axios'
import { API_BASE_URL } from '../../utils/configurations'
import { WarningView } from 'types/dto/warning'
import { Container } from 'components/@layout/Container'
import { getToastTitle } from '../../utils/toastTitle'

export const Warning: React.FC = () => {
  const [warning, setWarning] = React.useState<WarningView | undefined>(undefined)
  const [closed, setClosed] = React.useState<boolean>(false)
  const closeWarning = () => setClosed(true)

  React.useEffect(() => {
    axios
      .get<WarningView>(`${API_BASE_URL}/api/warning`)
      .then((res) => {
        if (res.data.message !== '') setWarning(res.data)
      })
      .catch(() => {
        console.error('Nem sikerült lekérni a figyelmeztetéseket.')
      })
  }, [setWarning])

  if (warning === undefined || closed) return null

  return (
    <Container>
      <Alert status="warning" variant="left-accent">
        <HStack justify="space-between" flex={1}>
          <AlertIcon />
          <VStack align="flex-start" flex={1}>
            <AlertTitle mr={2}>{getToastTitle(warning.type)}</AlertTitle>
            <AlertDescription wordBreak="break-word">{warning.message}</AlertDescription>
          </VStack>
          <CloseButton onClick={closeWarning} />
        </HStack>
      </Alert>
    </Container>
  )
}
