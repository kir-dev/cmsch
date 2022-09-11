import { Box, CloseButton, Flex, Heading, Text } from '@chakra-ui/react'
import { forwardRef } from 'react'
import { GroupMemberLocationView } from '../../../util/views/groupMemberLocation.view'
import { timestampToTimePassedStr } from '../util/timestampToTimePassedStr'

type PopupProps = {
  person?: GroupMemberLocationView
  onClose: () => void
}

export const Popup = forwardRef<HTMLDivElement, PopupProps>(({ person, onClose }, ref) => {
  return (
    <Box ref={ref} hidden={!person} bg="white" color="black" zIndex={1} borderRadius="1rem" p="0.5rem">
      <Flex justify="space-between" align="center">
        <Heading fontSize="sm" mt="0">
          {person?.groupName || ''}
        </Heading>
        <CloseButton onClick={onClose} />
      </Flex>
      <Text>{person?.alias}</Text>
      <Text>{person?.userName}</Text>
      <Text as="i">{timestampToTimePassedStr(person?.timestamp)}</Text>
    </Box>
  )
})
