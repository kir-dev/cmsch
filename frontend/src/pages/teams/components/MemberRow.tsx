import { useOpaqueBackground } from '@/util/core-functions.util'
import { useRef, useState } from 'react'

import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle
} from '@/components/ui/alert-dialog'
import type { TeamMemberView } from '@/util/views/team.view'
import { AcceptButton } from './AcceptButton'
import { DeleteButton } from './DeleteButton'
import { LeaderButton } from './LeaderButton'
import { RoleButton } from './RoleButton'

interface MemberRowProps {
  member: TeamMemberView
  onPromoteLeadership?: () => void
  onRoleChange?: () => void
  onAccept?: () => void
  onDelete?: () => void
}

export function MemberRow({ member, onDelete, onAccept, onRoleChange, onPromoteLeadership }: MemberRowProps) {
  const bg = useOpaqueBackground(1)
  const [isOpen, setIsOpen] = useState(false)
  const onContinue = useRef(() => {})
  const [title, setTitle] = useState('')
  const [prompt, setPrompt] = useState('')
  const dialogAction = (title: string, prompt: string, action: () => void) => {
    return () => {
      setTitle(title)
      setPrompt(prompt)
      setIsOpen(true)
      onContinue.current = action
    }
  }
  return (
    <>
      <div className="rounded-lg p-4 mt-5 flex flex-row items-center space-x-4" style={{ backgroundColor: bg }}>
        <div className="flex flex-col items-start overflow-hidden flex-1">
          <h3 className="text-xl font-bold m-0 w-full truncate">{member.name}</h3>
          {!onAccept && <p className="text-sm text-muted-foreground">{member.isAdmin ? 'Vezetőségi tag' : 'Tag'}</p>}
        </div>
        <div className="flex flex-row space-x-2">
          {onPromoteLeadership && (
            <LeaderButton
              onPromoteLeadership={dialogAction(
                'Csapatkapitány váltás',
                'Biztosan átadod a csapatkapitányságot neki: ' + member.name,
                onPromoteLeadership
              )}
            />
          )}
          {onRoleChange && (
            <RoleButton
              isAdmin={member.isAdmin}
              onRoleChange={dialogAction(
                member.isAdmin ? 'Jogosultság elvétele' : 'Jogosultság adása',
                `Biztosan jogosultságot változtatsz nála: ${member.name}?`,
                onRoleChange
              )}
            />
          )}
          {onAccept && <AcceptButton onAccept={onAccept} />}
          {onDelete && (
            <DeleteButton
              onDelete={onAccept ? onDelete : dialogAction('Tag törlése', `Biztosan szeretnéd törölni őt: ${member.name}?`, onDelete)}
            />
          )}
        </div>
      </div>
      <AlertDialog open={isOpen} onOpenChange={setIsOpen}>
        <AlertDialogContent>
          <AlertDialogHeader>
            <AlertDialogTitle>{title}</AlertDialogTitle>
            <AlertDialogDescription>{prompt}</AlertDialogDescription>
          </AlertDialogHeader>
          <AlertDialogFooter>
            <AlertDialogCancel>Mégse</AlertDialogCancel>
            <AlertDialogAction
              className="bg-destructive text-destructive-foreground hover:bg-destructive/90"
              onClick={() => {
                onContinue.current()
                setIsOpen(false)
              }}
            >
              Rendben
            </AlertDialogAction>
          </AlertDialogFooter>
        </AlertDialogContent>
      </AlertDialog>
    </>
  )
}
