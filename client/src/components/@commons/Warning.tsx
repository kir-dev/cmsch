import { Alert, AlertDescription, AlertIcon, CloseButton, HStack, VStack } from '@chakra-ui/react'
import axios from 'axios'
import { Container } from 'components/@layout/Container'
import { FC, useEffect, useState } from 'react'
import { WarningView } from 'types/dto/warning'

export const Warning: FC = () => {
  const [warning, setWarning] = useState<WarningView | undefined>(undefined)
  const [closed, setClosed] = useState<boolean>(false)
  const closeWarning = () => setClosed(true)

  useEffect(() => {
    axios
      .get<WarningView>(`/api/warning`)
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
      <Alert status={warning?.type || 'warning'} variant="left-accent">
        <HStack justify="space-between" flex={1}>
          <AlertIcon />
          <VStack align="flex-start" flex={1}>
            <AlertDescription wordBreak="break-word">{warning.message}</AlertDescription>
          </VStack>
          <CloseButton onClick={closeWarning} />
        </HStack>
      </Alert>
    </Container>
  )
}
