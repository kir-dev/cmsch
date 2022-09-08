import { Button } from '@chakra-ui/react'
import { DeleteIcon } from '@chakra-ui/icons'

interface DeleteButtonProps {
  onDelete: () => void
}

export function DeleteButton({ onDelete }: DeleteButtonProps) {
  return (
    <Button colorScheme="red" variant="outline" onClick={onDelete}>
      <DeleteIcon />
    </Button>
  )
}
