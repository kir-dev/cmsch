import { Tower } from '../../../util/views/qrFight.view'
import { Divider, Stat, StatHelpText, StatLabel, StatNumber, VStack } from '@chakra-ui/react'
import { useOpaqueBackground } from '../../../util/core-functions.util'

interface TowerFieldProps {
  tower: Tower
}

export function TowerField({ tower }: TowerFieldProps) {
  const background = useOpaqueBackground(3)

  return (
    <Stat flexBasis={'10em'} padding={5} borderRadius={5} background={background}>
      <StatNumber>{tower.name}</StatNumber>
      {tower.description && <StatLabel mt={1}>{tower.description}</StatLabel>}
      <VStack align="flex-start" mt={3}>
        <VStack align="flex-start" spacing={0}>
          <StatHelpText>Aktuális foglaló</StatHelpText>
          <StatLabel>{tower.ownerNow || 'Nincs'}</StatLabel>
        </VStack>
        <Divider />
        <VStack align="flex-start" spacing={0}>
          <StatHelpText>Helytartó</StatHelpText>
          <StatLabel>{tower.holder || 'Nincs'}</StatLabel>
          {tower.holdingFor !== undefined && tower.holder && <StatLabel>~{tower.holdingFor} perce</StatLabel>}
        </VStack>
      </VStack>
    </Stat>
  )
}
