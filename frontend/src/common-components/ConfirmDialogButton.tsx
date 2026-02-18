import {
  AlertDialog,
  AlertDialogBody,
  AlertDialogCloseButton,
  AlertDialogContent,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogOverlay,
  Button,
  type ResponsiveValue,
  useDisclosure
} from '@chakra-ui/react'
import { useRef, useState } from 'react'
import { useBrandColor } from '../util/core-functions.util.ts'

interface ConfirmDialogButtonProps {
  headerText?: string
  bodyText?: string
  buttonText?: string
  buttonColorScheme?: string
  buttonVariant?: string
  confirmButtonText?: string
  refuseButtonText?: string
  buttonWidth?: ResponsiveValue<string | number>

  // allow async confirm actions
  confirmAction(): Promise<void> | void
}

export const ConfirmDialogButton = ({
  headerText,
  bodyText,
  buttonText = '',
  buttonColorScheme,
  buttonVariant,
  buttonWidth,
  confirmButtonText = 'Igen',
  refuseButtonText = 'Mégse',
  confirmAction
}: ConfirmDialogButtonProps) => {
  const { isOpen, onOpen, onClose } = useDisclosure()
  const cancelRef = useRef(null)
  const brandColor = useBrandColor()
  const [isConfirming, setIsConfirming] = useState(false)

  const handleConfirm = async () => {
    try {
      setIsConfirming(true)
      // support sync and async confirmAction
      await Promise.resolve(confirmAction())
      onClose()
    } finally {
      setIsConfirming(false)
    }
  }

  return (
    <>
      <Button onClick={onOpen} width={buttonWidth} colorScheme={buttonColorScheme} variant={buttonVariant}>
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
            <Button ref={cancelRef} onClick={onClose} isDisabled={isConfirming}>
              {refuseButtonText}
            </Button>
            <Button colorScheme={brandColor} ml={3} onClick={handleConfirm} isLoading={isConfirming}>
              {confirmButtonText}
            </Button>
          </AlertDialogFooter>
        </AlertDialogContent>
      </AlertDialog>
    </>
  )
}
