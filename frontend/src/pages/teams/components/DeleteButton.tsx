import { Button, Tooltip } from '@chakra-ui/react'
import { DeleteIcon } from '@chakra-ui/icons'

interface DeleteButtonProps {
  onDelete: () => void
}

export function DeleteButton({ onDelete }: DeleteButtonProps) {
  return (
    <Tooltip label="Törlés">
      <Button colorScheme="red" variant="outline" onClick={onDelete}>
        <DeleteIcon />
      </Button>
    </Tooltip>
  )
}
