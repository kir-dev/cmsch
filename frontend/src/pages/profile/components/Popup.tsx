import { Box } from '@chakra-ui/layout'
import { forwardRef } from 'react'
import { GroupMemberLocationView } from '../../../util/views/groupMemberLocation.view'

type PopupProps = {
  person?: GroupMemberLocationView
}

export const Popup = forwardRef<HTMLDivElement, PopupProps>(({ person }, ref) => {
  if (!person) return null
  return (
    <Box ref={ref} style={{ backgroundColor: 'white', color: 'black', position: 'absolute' }}>
      {person.alias}: {person.timestamp}
    </Box>
  )
})
