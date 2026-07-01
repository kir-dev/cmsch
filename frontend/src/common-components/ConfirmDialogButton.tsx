import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
  AlertDialogTrigger
} from '@/components/ui/alert-dialog'
import { Button, buttonVariants } from '@/components/ui/button'
import type { VariantProps } from 'class-variance-authority'
import { Loader2 } from 'lucide-react'
import { useState } from 'react'

interface ConfirmDialogButtonProps {
  headerText?: string
  bodyText?: string
  buttonText?: string
  buttonColorScheme?: string
  buttonVariant?: VariantProps<typeof buttonVariants>['variant']
  confirmButtonText?: string
  refuseButtonText?: string
  buttonWidth?: string

  confirmAction(): void | Promise<void>
}

export const ConfirmDialogButton = ({
  headerText,
  bodyText,
  buttonText = '',
  buttonColorScheme,
  buttonVariant,
  confirmButtonText = 'Igen',
  refuseButtonText = 'Mégse',
  confirmAction
}: ConfirmDialogButtonProps) => {
  const [isPending, setIsPending] = useState(false)
  const resolvedVariant = buttonColorScheme === 'red' ? 'destructive' : buttonVariant

  const handleConfirm = async () => {
    setIsPending(true)
    try {
      await confirmAction()
    } finally {
      setIsPending(false)
    }
  }

  return (
    <AlertDialog>
      <AlertDialogTrigger asChild>
        <Button variant={resolvedVariant}>{buttonText}</Button>
      </AlertDialogTrigger>
      <AlertDialogContent>
        <AlertDialogHeader>
          {headerText && <AlertDialogTitle>{headerText}</AlertDialogTitle>}
          {bodyText && <AlertDialogDescription>{bodyText}</AlertDialogDescription>}
        </AlertDialogHeader>
        <AlertDialogFooter>
          <AlertDialogCancel disabled={isPending}>{refuseButtonText}</AlertDialogCancel>
          <AlertDialogAction onClick={handleConfirm} disabled={isPending}>
            {isPending && <Loader2 className="mr-2 h-4 w-4 animate-spin" />}
            {confirmButtonText}
          </AlertDialogAction>
        </AlertDialogFooter>
      </AlertDialogContent>
    </AlertDialog>
  )
}
