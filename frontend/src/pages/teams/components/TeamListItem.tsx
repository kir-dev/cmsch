import { ChevronRightIcon } from '@chakra-ui/icons'
import { Box, Heading, HStack, Spacer, Text, VStack } from '@chakra-ui/react'
import { useColorModeValue } from '@chakra-ui/system'
import { Link } from 'react-router-dom'

import { TeamView } from '../../../util/views/team.view'
import { AbsolutePaths } from '../../../util/paths'

type TeamListItemProps = {
  team: TeamView
}

export const TeamListItem = ({ team }: TeamListItemProps) => {
  return (
    <Link to={AbsolutePaths.TEAM + '/details/' + team.id}>
      <Box
        borderRadius="lg"
        padding={4}
        backgroundColor={useColorModeValue('brand.200', 'brand.600')}
        marginTop={5}
        transition="transform .2s ease-in-out"
        _hover={{ transform: 'translateX(0.5em)' }}
      >
        <HStack spacing={4}>
          <VStack align="flex-start" overflow="hidden">
            <Heading as="h3" size="md" marginY={0} maxWidth="100%">
              {team.name}
            </Heading>
          </VStack>
          <Spacer />
          <VStack spacing={2} display={['none', 'flex']}>
            <Text>{team.points} pont</Text>
            <Text>{team.members.length} tag</Text>
          </VStack>
          <ChevronRightIcon boxSize={{ base: 10, md: 16 }} color="gray.300" />
        </HStack>
      </Box>
    </Link>
  )
}
