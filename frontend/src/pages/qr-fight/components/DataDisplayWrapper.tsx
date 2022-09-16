import { Heading, HStack, Text, VStack } from '@chakra-ui/react'

import { LevelStatus, QrLevelDto } from '../../../util/views/qrFight.view'
import { LevelStatusBadge } from './LevelStatusBadge'
import { LevelDataDisplay } from './LevelDataDisplay'
import { TowerDataDisplay } from './TowerDataDisplay'
import { useColorModeValue } from '@chakra-ui/system'

interface DataDisplayWrapperProps {
  level: QrLevelDto
}
export function DataDisplayWrapper({ level }: DataDisplayWrapperProps) {
  return (
    <VStack
      spacing={5}
      p={5}
      mt={5}
      borderRadius={10}
      borderColor={useColorModeValue('gray.400', 'gray.600')}
      borderWidth="1px"
      opacity={level.status === LevelStatus.COMPLETED || level.status === LevelStatus.OPEN ? 1 : 0.2}
    >
      <HStack justifyContent="flex-start" w="100%">
        <VStack align="flex-start">
          <Heading m={0} fontSize="xl">
            {level.name}
          </Heading>
          <LevelStatusBadge level={level} />
          <Text>Birtokló: {level.owner}</Text>
          <Text>{level.description}</Text>
        </VStack>
      </HStack>
      <LevelDataDisplay level={level} />
      {level.towers?.length > 0 && <TowerDataDisplay level={level} />}
    </VStack>
  )
}
