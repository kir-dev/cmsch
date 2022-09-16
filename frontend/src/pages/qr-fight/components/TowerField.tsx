import { Tower } from '../../../util/views/qrFight.view'
import { Divider, Stat, StatHelpText, StatLabel, StatNumber, VStack } from '@chakra-ui/react'
import { useColorModeValue } from '@chakra-ui/system'

interface TowerFieldProps {
  tower: Tower
}

export function TowerField({ tower }: TowerFieldProps) {
  return (
    <Stat width={'fit-content'} borderWidth="1px" borderColor={useColorModeValue('gray.400', 'gray.600')} padding={5} borderRadius={5}>
      <StatNumber>{tower.name}</StatNumber>
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
