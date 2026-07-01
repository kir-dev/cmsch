import { Badge } from '@/components/ui/badge'

interface EventTagsProps {
  tags: Array<string>
  className?: string
}

const EventTags = ({ tags, className }: EventTagsProps) => {
  return (
    <div className={`flex justify-end gap-2 ${className}`}>
      {tags.filter(Boolean).map((tag) => {
        return (
          <Badge variant="default" key={tag}>
            {tag}
          </Badge>
        )
      })}
    </div>
  )
}

export default EventTags
