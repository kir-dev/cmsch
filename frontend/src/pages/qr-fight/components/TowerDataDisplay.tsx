import { QrLevelDto } from '../../../util/views/qrFight.view'
import { Flex, Heading, VStack } from '@chakra-ui/react'
import { TowerField } from './TowerField'

interface TowerDataDisplay {
  level: QrLevelDto
}

export function TowerDataDisplay({ level }: TowerDataDisplay) {
  return (
    <VStack align="flex-start" w="100%">
      <Heading fontSize="lg">Tornyok</Heading>
      <Flex wrap="wrap" w="100%" gap={5}>
        {level.towers.map((t) => (
          <TowerField tower={t} key={t.name} />
        ))}
      </Flex>
    </VStack>
  )
}
