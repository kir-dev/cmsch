import {
  AlertDialog,
  AlertDialogOverlay,
  AlertDialogContent,
  AlertDialogHeader,
  AlertDialogCloseButton,
  AlertDialogBody,
  AlertDialogFooter,
  Button,
  useDisclosure
} from '@chakra-ui/react'
import React from 'react'

interface ConfirmDialogButtonProps {
  headerText?: string
  bodyText?: string
  buttonText?: string
  buttonColorSchene?: string
  buttonVariant?: string
  confirmButtonText?: string
  refuseButtonText?: string
  confirmAction(): void
}

export const ConfirmDialogButton = ({
  headerText,
  bodyText,
  buttonText = '',
  buttonColorSchene,
  buttonVariant,
  confirmButtonText = 'Igen',
  refuseButtonText = 'Nem',
  confirmAction
}: ConfirmDialogButtonProps) => {
  const { isOpen, onOpen, onClose } = useDisclosure()
  const cancelRef = React.useRef(null)

  return (
    <>
      <Button onClick={onOpen} width="100%" colorScheme={buttonColorSchene} variant={buttonVariant}>
        {buttonText}
      </Button>
      <AlertDialog
        preserveScrollBarGap={true}
        motionPreset="slideInBottom"
        leastDestructiveRef={cancelRef}
        onClose={onClose}
        isOpen={isOpen}
        isCentered
      >
        <AlertDialogOverlay />
        <AlertDialogContent>
          {headerText && <AlertDialogHeader>{headerText}</AlertDialogHeader>}
          <AlertDialogCloseButton />
          {bodyText && <AlertDialogBody>{bodyText}</AlertDialogBody>}
          <AlertDialogFooter>
            <Button ref={cancelRef} onClick={onClose}>
              {refuseButtonText}
            </Button>
            <Button colorScheme="red" ml={3} onClick={confirmAction}>
              {confirmButtonText}
            </Button>
          </AlertDialogFooter>
        </AlertDialogContent>
      </AlertDialog>
    </>
  )
}
