import type { Totem } from '../../../util/views/qrFight.view'
import { Stat, StatHelpText, StatLabel, StatNumber, VStack } from '@chakra-ui/react'
import { useOpaqueBackground } from '../../../util/core-functions.util'

interface TotemFieldProps {
  totem: Totem
}

export function TotemField({ totem }: TotemFieldProps) {
  const background = useOpaqueBackground(3)

  return (
    <Stat flexBasis={'10em'} padding={5} borderRadius={5} background={background}>
      <StatNumber>{totem.name}</StatNumber>
      {totem.description && <StatLabel mt={1}>{totem.description}</StatLabel>}
      <VStack align="flex-start" spacing={0} mt={3}>
        <StatHelpText>Befoglalta</StatHelpText>
        <StatLabel>{totem.owner || 'Még senki'}</StatLabel>
      </VStack>
    </Stat>
  )
}
