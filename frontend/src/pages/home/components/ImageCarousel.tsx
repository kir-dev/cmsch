import { ChevronLeftIcon, ChevronRightIcon } from '@chakra-ui/icons'
import { Box, Button, ButtonGroup, Flex, Image, useColorModeValue } from '@chakra-ui/react'
import { useState } from 'react'
import { MapImageView } from '../../../util/views/mapImage.view'

type ImageCarouselProps = {
  images: MapImageView[]
}

export const ImageCarousel = ({ images }: ImageCarouselProps) => {
  const [currentImageIndex, setCurrentImageIndex] = useState<number>(0)
  const previousImage = () => {
    const previousIndex = currentImageIndex - 1
    setCurrentImageIndex(previousIndex < 0 ? images.length - 1 : previousIndex)
  }
  const nextImage = () => {
    const nextIndex = currentImageIndex + 1
    setCurrentImageIndex(nextIndex > images.length - 1 ? 0 : nextIndex)
  }
  if (images.length === 0) return null
  return (
    <Box marginTop={10}>
      {images.map((image, index) => (
        <Image
          key={index}
          src={useColorModeValue(image.light, image.dark)}
          w="100%"
          maxH="50rem"
          objectFit="contain"
          display={index === currentImageIndex ? 'block' : 'none'}
        />
      ))}
      <Flex marginTop={5} justify="space-between">
        <DirectionButton direction={Directions.LEFT} onClick={previousImage} />
        <ButtonGroup>
          {images.map((_image, index) => (
            <CurrentImageIndicatorDot key={index} index={index} currentIndex={currentImageIndex} onClick={setCurrentImageIndex} />
          ))}
        </ButtonGroup>
        <DirectionButton direction={Directions.RIGHT} onClick={nextImage} />
      </Flex>
    </Box>
  )
}

type CurrentImageIndicatorDotProps = {
  index: number
  currentIndex: number
  onClick: (index: number) => void
}

const CurrentImageIndicatorDot = ({ index, currentIndex, onClick }: CurrentImageIndicatorDotProps) => (
  <Button
    height={10}
    width={10}
    padding={0}
    borderWidth={2}
    borderStyle="solid"
    borderColor="gray.500"
    borderRadius="full"
    cursor="pointer"
    transition="border-width .1s"
    _hover={{ borderWidth: 10 }}
    backgroundColor={index === currentIndex ? 'gray.500' : 'transparent'}
    onClick={() => {
      onClick(index)
    }}
  />
)
type DirectionButtonProps = {
  direction: Directions
  onClick: () => void
}

enum Directions {
  LEFT = 'left',
  RIGHT = 'right'
}

const DirectionButton = ({ direction, onClick }: DirectionButtonProps) => (
  <Button onClick={onClick} fontSize="6xl" padding={0} variant="ghost" color="gray.500">
    {direction === Directions.LEFT ? <ChevronLeftIcon /> : <ChevronRightIcon />}
  </Button>
)
