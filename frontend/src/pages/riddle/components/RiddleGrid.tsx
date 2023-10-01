import { Box, Button, HStack, Image, VStack, Wrap } from '@chakra-ui/react'
import { useState } from 'react'
import { FaArrowCircleRight, FaCheckCircle } from 'react-icons/fa'
import { useOpaqueBackground } from '../../../util/core-functions.util'
import { Riddle } from '../../../util/views/riddle.view'

interface RiddleGridProps {
  riddleList: Riddle[]
  onClick: (idx: number) => void
  selected: number
}

export function RiddleGrid({ riddleList, onClick, selected }: RiddleGridProps) {
  const [isSelected, setIsSelected] = useState<number>(selected)
  const bg = useOpaqueBackground(6)
  const bgHover = useOpaqueBackground(4)

  return (
    <Wrap my={2}>
      {riddleList.map((r, idx) => (
        <Button
          bg={idx === isSelected ? bg : undefined}
          _hover={{ bg: bgHover }}
          p={1}
          height={100}
          onClick={() => {
            setIsSelected(idx)
            onClick(idx)
          }}
        >
          <VStack>
            <Image objectFit="contain" objectPosition="center" width="60px" height="60px" src={r.imageUrl}></Image>
            <HStack justify="center">
              <Box>{idx + 1}</Box>
              {r.solved && (
                <Box color={r.skipped ? 'yellow.400' : 'green.400'}>{r.skipped ? <FaArrowCircleRight /> : <FaCheckCircle />}</Box>
              )}
            </HStack>
          </VStack>
        </Button>
      ))}
    </Wrap>
  )
}
