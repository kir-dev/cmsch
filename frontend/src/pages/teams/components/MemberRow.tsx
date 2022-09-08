import { useColorModeValue } from '@chakra-ui/system'
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
import { useRef } from 'react'

import { TeamMemberView } from '../../../util/views/team.view'
import { DeleteButton } from './DeleteButton'
import { RoleButton } from './RoleButton'
import { AcceptButton } from './AcceptButton'
import { useServiceContext } from '../../../api/contexts/service/ServiceContext'

interface MemberRowProps {
  member: TeamMemberView
  onRoleChange?: () => void
  onAccept?: () => void
  onDelete?: () => void
}

export function MemberRow({ member, onDelete, onAccept, onRoleChange }: MemberRowProps) {
  const { isOpen, onOpen, onClose } = useDisclosure()
  const cancelRef = useRef<any>()
  const { sendMessage } = useServiceContext()
  return (
    <>
      <Box borderRadius="lg" padding={4} backgroundColor={useColorModeValue('brand.200', 'brand.600')} marginTop={5}>
        <HStack spacing={4}>
          <VStack align="flex-start" overflow="hidden">
            <Heading as="h3" size="md" marginY={0} maxWidth="100%">
              {member.name}
            </Heading>
            {!onAccept && <Text>{member.isAdmin ? 'Admin' : 'Tag'}</Text>}
          </VStack>
          <Spacer />
          <HStack>
            {onRoleChange && (
              <RoleButton
                isAdmin={member.isAdmin}
                onRoleChange={() => {
                  onRoleChange()
                  sendMessage('Jogosultság módosítva!', { toast: true, toastStatus: 'warning' })
                }}
              />
            )}
            {onAccept && (
              <AcceptButton
                onAccept={() => {
                  onAccept()
                  sendMessage('Jelentkezés elfogadva!', { toast: true, toastStatus: 'success' })
                }}
              />
            )}
            {onDelete && <DeleteButton onDelete={onOpen} />}
          </HStack>
        </HStack>
      </Box>
      {onDelete && (
        <AlertDialog isOpen={isOpen} leastDestructiveRef={cancelRef} onClose={onClose}>
          <AlertDialogOverlay>
            <AlertDialogContent>
              <AlertDialogHeader fontSize="lg" fontWeight="bold">
                {onAccept ? 'Jelentkező' : 'Tag'} eltávolítása
              </AlertDialogHeader>

              <AlertDialogBody>Biztosan szeretnéd törölni őt: {member.name}?</AlertDialogBody>

              <AlertDialogFooter>
                <Button ref={cancelRef} onClick={onClose}>
                  Mégse
                </Button>
                <Button
                  colorScheme="red"
                  onClick={() => {
                    onDelete()
                    onClose()
                  }}
                  ml={3}
                >
                  Törlés
                </Button>
              </AlertDialogFooter>
            </AlertDialogContent>
          </AlertDialogOverlay>
        </AlertDialog>
      )}
    </>
  )
}
