import { Avatar, HStack, Tag, Text, useColorModeValue, VStack, WrapItem } from '@chakra-ui/react'
import { Organizer } from '../../../api/contexts/config/types'
import { customTheme } from '../../../util/configs/theme.config'

type Props = {
  organizer: Organizer
}

const OrganizerCard = ({ organizer: { name, avatar, roles } }: Props) => {
  return (
    <WrapItem border="2px" borderColor={useColorModeValue('gray.200', 'gray.700')} borderRadius="md">
      <HStack align="center" w="20rem" p={2}>
        <Avatar name={name} src={avatar} size="lg" />
        <VStack flex={1} align="stretch" spacing={0}>
          <Text fontSize="lg" fontWeight={700}>
            {name}
          </Text>
          <Text fontSize="sm" fontStyle="italic">
            {roles}
          </Text>
        </VStack>
      </HStack>
    </WrapItem>
  )
}

export default OrganizerCard
