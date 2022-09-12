import { HStack, SpaceProps, Tag } from '@chakra-ui/react'

interface EventTagsProps {
  tags: Array<string>
  my?: SpaceProps['my']
}

const EventTags = ({ tags, my }: EventTagsProps) => {
  return (
    <HStack spacing={2} my={my} justifyContent="end">
      {tags.map((tag) => {
        return (
          <>
            {tag && (
              <Tag size={'md'} variant="solid" colorScheme="brand" key={tag}>
                {tag}
              </Tag>
            )}
          </>
        )
      })}
    </HStack>
  )
}

export default EventTags
