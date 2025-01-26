import { Button } from '@chakra-ui/react'
import {
  DialogActionTrigger,
  DialogBody,
  DialogCloseTrigger,
  DialogContent,
  DialogFooter,
  DialogHeader,
  DialogRoot,
  DialogTrigger
} from '../components/ui/dialog.tsx'

interface ConfirmDialogButtonProps {
  headerText?: string
  bodyText?: string
  buttonText?: string
  buttonColorScheme?: string
  buttonVariant?: 'solid' | 'subtle' | 'surface' | 'outline' | 'ghost' | 'plain'
  confirmButtonText?: string
  refuseButtonText?: string
  buttonWidth?: string

  confirmAction(): void
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
  return (
    <>
      <DialogRoot motionPreset="slide-in-bottom" placement="center">
        <DialogTrigger asChild>
          <Button width={buttonWidth} colorScheme={buttonColorScheme} variant={buttonVariant}>
            {buttonText}
          </Button>
        </DialogTrigger>
        <DialogContent>
          {headerText && <DialogHeader>{headerText}</DialogHeader>}
          <DialogCloseTrigger />
          {bodyText && <DialogBody>{bodyText}</DialogBody>}
          <DialogFooter>
            <DialogActionTrigger>
              <Button>{refuseButtonText}</Button>
            </DialogActionTrigger>
            <Button colorScheme="brand" ml={3} onClick={confirmAction}>
              {confirmButtonText}
            </Button>
          </DialogFooter>
        </DialogContent>
      </DialogRoot>
    </>
  )
}
