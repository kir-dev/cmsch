import { HStack, type SpaceProps, Tag } from '@chakra-ui/react'
import { useBrandColor } from '../../../util/core-functions.util.ts'

interface EventTagsProps {
  tags: Array<string>
  my?: SpaceProps['my']
}

const EventTags = ({ tags, my }: EventTagsProps) => {
  const brandColor = useBrandColor()
  return (
    <HStack spacing={2} my={my} justifyContent="end">
      {tags.filter(Boolean).map((tag) => {
        return (
          <Tag size={'md'} variant="solid" colorScheme={brandColor} key={tag}>
            {tag}
          </Tag>
        )
      })}
    </HStack>
  )
}

export default EventTags
