import { Heading, HStack, Text, VStack } from '@chakra-ui/react'

import { LevelStatus, QrLevelDto } from '../../../util/views/qrFight.view'
import { LevelStatusBadge } from './LevelStatusBadge'
import { LevelDataDisplay } from './LevelDataDisplay'
import { TowerDataDisplay } from './TowerDataDisplay'
import Markdown from '../../../common-components/Markdown'
import { useOpaqueBackground } from '../../../util/core-functions.util'
import _ from 'lodash'
import { TotemDataDisplay } from './TotemDataDisplay'

interface DataDisplayWrapperProps {
  level: QrLevelDto
}

export function DataDisplayWrapper({ level }: DataDisplayWrapperProps) {
  const backgroundColor = useOpaqueBackground(3)

  return (
    <VStack
      spacing={5}
      p={5}
      mt={5}
      borderRadius={10}
      bg={backgroundColor}
      opacity={level.status === LevelStatus.COMPLETED || level.status === LevelStatus.OPEN ? 1 : 0.2}
    >
      <HStack justifyContent="flex-start" w="100%">
        <VStack align="flex-start">
          <Heading m={0} fontSize="2xl">
            {level.name}
          </Heading>
          <LevelStatusBadge level={level} />
          <Text>Birtokló: {level.owners}</Text>
          <Text>A te csapatodnak van: {level.tokenCount}db</Text>
          <Markdown text={level.description} />
        </VStack>
      </HStack>
      {!_.isEmpty(level.teams) && <LevelDataDisplay teams={level.teams} />}
      {level.towers?.length > 0 && <TowerDataDisplay level={level} />}
      {level.totems?.length > 0 && <TotemDataDisplay level={level} />}
    </VStack>
  )
}
