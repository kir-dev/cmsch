import {
  AlertDialog,
  AlertDialogBody,
  AlertDialogContent,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogOverlay,
  Box,
  Button,
  Heading,
  HStack,
  Spacer,
  Text,
  useDisclosure,
  VStack
} from '@chakra-ui/react'
import { useRef, useState } from 'react'
import { useOpaqueBackground } from '../../../util/core-functions.util'

import { TeamMemberView } from '../../../util/views/team.view'
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
  const { isOpen, onOpen, onClose } = useDisclosure()
  const onContinue = useRef(() => {})
  const [title, setTitle] = useState('')
  const [prompt, setPrompt] = useState('')
  const cancelRef = useRef<any>()
  const dialogAction = (title: string, prompt: string, action: () => void) => {
    return () => {
      setTitle(title)
      setPrompt(prompt)
      onOpen()
      onContinue.current = action
    }
  }
  return (
    <>
      <Box borderRadius="lg" padding={4} backgroundColor={bg} marginTop={5}>
        <HStack spacing={4}>
          <VStack align="flex-start" overflow="hidden">
            <Heading as="h3" size="md" marginY={0} maxWidth="100%">
              {member.name}
            </Heading>
            {!onAccept && <Text>{member.admin ? 'Vezetőségi tag' : 'Tag'}</Text>}
          </VStack>
          <Spacer />
          <HStack>
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
                isAdmin={member.admin}
                onRoleChange={dialogAction(
                  member.admin ? 'Jogosultság elvétele' : 'Jogosultság adása',
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
          </HStack>
        </HStack>
      </Box>
      <AlertDialog isOpen={isOpen} leastDestructiveRef={cancelRef} onClose={onClose}>
        <AlertDialogOverlay>
          <AlertDialogContent>
            <AlertDialogHeader fontSize="lg" fontWeight="bold">
              {title}
            </AlertDialogHeader>
            <AlertDialogBody>{prompt}</AlertDialogBody>
            <AlertDialogFooter>
              <Button ref={cancelRef} onClick={onClose}>
                Mégse
              </Button>
              <Button
                colorScheme="red"
                onClick={() => {
                  onContinue.current()
                  onClose()
                }}
                ml={3}
              >
                Rendben
              </Button>
            </AlertDialogFooter>
          </AlertDialogContent>
        </AlertDialogOverlay>
      </AlertDialog>
    </>
  )
}
