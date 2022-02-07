import React, { useState } from 'react'
import { Alert, AlertDescription, AlertIcon, CloseButton, Flex, HStack } from '@chakra-ui/react'
import { Container } from 'components/@layout/Container'
import { LinkButton } from './LinkButton'
import { useLocation } from 'react-router-dom'
import { useAuthContext } from '../../utils/useAuthContext'

export const GroupSelectionWarning: React.FC = () => {
  const [closed, setClosed] = useState<boolean>(false)
  const closeWarning = () => setClosed(true)
  const { pathname } = useLocation()
  const { profile } = useAuthContext()
  if (!profile?.groupSelectionAllowed || closed || pathname === '/profil/tankor-modositas') return null

  return (
    <Container>
      <Alert status="error" variant="left-accent">
        <HStack justify="space-between" flex={1}>
          <AlertIcon />
          <Flex align="center" justify="flex-start" flex={1} flexDirection={['column', 'column', 'row']}>
            <AlertDescription wordBreak="break-word">Módosítanod kell a tankörödet feladatok eléréséhez!</AlertDescription>
            <LinkButton href="/profil/tankor-modositas" colorScheme="red" m={3}>
              Módosítás most
            </LinkButton>
          </Flex>
          <CloseButton onClick={closeWarning} />
        </HStack>
      </Alert>
    </Container>
  )
}
