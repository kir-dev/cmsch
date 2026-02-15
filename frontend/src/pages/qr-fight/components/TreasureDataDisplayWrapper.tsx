import { Heading, HStack, Text, VStack } from '@chakra-ui/react'

import isEmpty from 'lodash/isEmpty'
import Markdown from '../../../common-components/Markdown'
import { useOpaqueBackground } from '../../../util/core-functions.util'
import { LevelStatus, type QrTreasureHuntDto } from '../../../util/views/qrFight.view'
import { LevelDataDisplay } from './LevelDataDisplay'
import { LevelStatusBadge } from './LevelStatusBadge'

interface TreasureDataDisplayWrapperProps {
  level: QrTreasureHuntDto
}

export function TreasureDataDisplayWrapper({ level }: TreasureDataDisplayWrapperProps) {
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
          <LevelStatusBadge levelStatus={level.status} />
          <Text>Birtokló: {level.owners}</Text>
          <Text>A te csapatodnak van: {level.tokenCount}db</Text>
          <Markdown text={level.description} />
        </VStack>
      </HStack>
      {!isEmpty(level.teams) && <LevelDataDisplay teams={level.teams} />}
      <HStack justifyContent="flex-start" w="90%" m={5}>
        <VStack align="flex-start">
          <Heading fontSize="xl" m={0}>
            Kincsek rejtekhelyei:
          </Heading>
          {level.foundTokens.map((token, index) => (
            <Text key={index}>- {token}</Text>
          ))}
        </VStack>
      </HStack>
    </VStack>
  )
}
