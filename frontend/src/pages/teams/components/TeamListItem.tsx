import { ChevronRightIcon } from '@chakra-ui/icons'
import { Box, Heading, HStack, Image, Spacer, VStack } from '@chakra-ui/react'
import { Link } from 'react-router'
import { useOpaqueBackground } from '../../../util/core-functions.util'
import { AbsolutePaths } from '../../../util/paths'

import { TeamListItemView } from '../../../util/views/team.view'
import TeamLabel from './TeamLabel.tsx'

type TeamListItemProps = {
  team: TeamListItemView
  detailEnabled?: boolean
}

export const TeamListItem = ({ team, detailEnabled = false }: TeamListItemProps) => {
  const bg = useOpaqueBackground(1)
  return (
    <Link to={AbsolutePaths.TEAMS + '/details/' + team.id}>
      <Box
        borderRadius="lg"
        padding={4}
        backgroundColor={bg}
        marginTop={5}
        transition={detailEnabled ? 'transform .2s ease-in-out' : undefined}
        _hover={{ transform: detailEnabled ? 'translateX(0.5em)' : undefined }}
      >
        <HStack spacing={4}>
          <VStack align="flex-start" overflow="hidden">
            <HStack spacing={4} alignItems="baseline">
              <Heading as="h3" size="md" marginY={0} maxWidth="100%">
                {team.name}
              </Heading>
              {team.labels && team.labels.map((label, index) => <TeamLabel label={label} key={index} />)}
            </HStack>
            {team.introduction && <Box>{team.introduction}</Box>}
          </VStack>
          <Spacer />
          {team.logo && (
            <Image
              display="block"
              src={team.logo}
              alt={team.name}
              w="64px"
              h="64px"
              objectFit="contain"
              alignSelf="center"
              borderRadius="md"
              loading="lazy"
            />
          )}
          {detailEnabled && <ChevronRightIcon boxSize={{ base: 10, md: 16 }} color="gray.300" />}
        </HStack>
      </Box>
    </Link>
  )
}
