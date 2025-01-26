import { HStack } from '@chakra-ui/react'
import { Tag } from '../../../components/ui/tag.tsx'

interface EventTagsProps {
  tags: Array<string>
  my?: number
}

const EventTags = ({ tags, my }: EventTagsProps) => {
  return (
    <HStack gap={2} my={my} justifyContent="end">
      {tags.filter(Boolean).map((tag) => {
        return (
          <Tag size={'md'} variant="solid" colorScheme="brand" key={tag}>
            {tag}
          </Tag>
        )
      })}
    </HStack>
  )
}

export default EventTags
