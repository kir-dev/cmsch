import type { Organizer } from '@/api/contexts/config/types'
import Markdown from '@/common-components/Markdown'
import { OrganizerWrapItem } from './OrganizerWrapItem'

type Props = {
  organizers: Organizer[]
  message: string | undefined
  title: string
}

export const OrganizerSection = ({ organizers, message, title }: Props) => {
  if (!message) return null

  return (
    <div className="mt-10">
      <h2 className="text-2xl font-bold">{title}</h2>
      {organizers.length > 0 && (
        <div className="flex flex-wrap justify-center gap-4 my-4">
          {organizers.map((organizer) => (
            <OrganizerWrapItem key={organizer.name} organizer={organizer} />
          ))}
        </div>
      )}
      <Markdown text={message} />
    </div>
  )
}
