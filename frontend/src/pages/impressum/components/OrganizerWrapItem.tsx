import type { Organizer } from '@/api/contexts/config/types'
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar'

type Props = {
  organizer: Organizer
}

export const OrganizerWrapItem = ({ organizer: { name, avatar, roles } }: Props) => {
  return (
    <div className="border border-border rounded-md bg-secondary text-secondary-foreground">
      <div className="flex flex-row items-center w-80 p-2 space-x-4">
        <Avatar className="h-12 w-12">
          <AvatarImage src={avatar} alt={name} />
          <AvatarFallback>{name.substring(0, 2).toUpperCase()}</AvatarFallback>
        </Avatar>
        <div className="flex flex-col flex-1 items-stretch space-y-0">
          <span className="text-lg font-bold">{name}</span>
          <span className="text-sm italic text-muted-foreground">{roles}</span>
        </div>
      </div>
    </div>
  )
}
