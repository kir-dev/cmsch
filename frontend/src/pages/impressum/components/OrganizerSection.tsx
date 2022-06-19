import { Box, Heading, Wrap } from '@chakra-ui/react'
import { Organizer } from '../../../api/contexts/config/types'
import Markdown from '../../../common-components/Markdown'
import { OrganizerWrapItem } from './OrganizerWrapItem'

type Props = {
  organizers: Organizer[]
  message: string | undefined
  title: string
}

export const OrganizerSection = ({ organizers, message, title }: Props) => {
  if (!message) return null

  return (
    <Box mt={10}>
      <Heading as="h2">{title}</Heading>
      {organizers.length > 0 && (
        <Wrap justify="center" my={4}>
          {organizers.map((organizer) => (
            <OrganizerWrapItem key={organizer.name} organizer={organizer} />
          ))}
        </Wrap>
      )}
      <Markdown text={message} />
    </Box>
  )
}
