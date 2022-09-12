import { ChevronRightIcon } from '@chakra-ui/icons'
import { Box, Heading, HStack, Spacer, VStack } from '@chakra-ui/react'
import { useColorModeValue } from '@chakra-ui/system'
import { Link } from 'react-router-dom'

import { TeamListItemView } from '../../../util/views/team.view'
import { AbsolutePaths } from '../../../util/paths'

type TeamListItemProps = {
  team: TeamListItemView
  detailEnabled?: boolean
}

export const TeamListItem = ({ team, detailEnabled = false }: TeamListItemProps) => {
  return (
    <Link to={AbsolutePaths.TEAMS + '/details/' + team.id}>
      <Box
        borderRadius="lg"
        padding={4}
        backgroundColor={useColorModeValue('brand.200', 'brand.600')}
        marginTop={5}
        transition={detailEnabled ? 'transform .2s ease-in-out' : undefined}
        _hover={{ transform: detailEnabled ? 'translateX(0.5em)' : undefined }}
      >
        <HStack spacing={4}>
          <VStack align="flex-start" overflow="hidden">
            <Heading as="h3" size="md" marginY={0} maxWidth="100%">
              {team.name}
            </Heading>
          </VStack>
          <Spacer />
          {detailEnabled && <ChevronRightIcon boxSize={{ base: 10, md: 16 }} color="gray.300" />}
        </HStack>
      </Box>
    </Link>
  )
}
