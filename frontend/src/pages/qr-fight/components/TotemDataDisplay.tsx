import { QrLevelDto } from '../../../util/views/qrFight.view'
import { Flex, Heading, VStack } from '@chakra-ui/react'
import { TotemField } from './TotemField'

interface TotemDataDisplay {
  level: QrLevelDto
}

export function TotemDataDisplay({ level }: TotemDataDisplay) {
  return (
    <VStack align="flex-start" w="100%">
      <Heading fontSize="lg">Totemek</Heading>
      <Flex wrap="wrap" w="100%" gap={5}>
        {level.totems.map((t) => (
          <TotemField totem={t} key={t.name} />
        ))}
      </Flex>
    </VStack>
  )
}
