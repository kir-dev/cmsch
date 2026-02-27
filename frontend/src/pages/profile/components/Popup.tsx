import { timestampToTimePassedStr } from '@/pages/profile/util/timestampToTimePassedStr'
import type { GroupMemberLocationView } from '@/util/views/groupMemberLocation.view'
import { X } from 'lucide-react'
import { forwardRef } from 'react'

type PopupProps = {
  person?: GroupMemberLocationView
  onClose: () => void
}

export const Popup = forwardRef<HTMLDivElement, PopupProps>(({ person, onClose }, ref) => {
  return (
    <div ref={ref} className={`bg-popover text-popover-foreground z-10 rounded-2xl p-2 border shadow-md ${!person ? 'hidden' : ''}`}>
      <div className="flex justify-between items-center">
        <h4 className="text-sm font-bold m-0">{person?.groupName || ''}</h4>
        <button onClick={onClose} className="p-1 hover:bg-accent rounded-full transition-colors">
          <X className="h-4 w-4" />
        </button>
      </div>
      <p>{person?.alias}</p>
      <p>{person?.userName}</p>
      <p className="italic">{timestampToTimePassedStr(person?.timestamp)}</p>
    </div>
  )
})
