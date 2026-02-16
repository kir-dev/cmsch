import { ChevronLeftIcon, ChevronRightIcon } from '@chakra-ui/icons'
import { Box, Button, ButtonGroup, Flex, IconButton, Image } from '@chakra-ui/react'
import { useState } from 'react'
import { useBrandColor } from '../../../util/core-functions.util.ts'

type ImageCarouselProps = {
  images: string[]
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
    <Flex flexDirection="column" marginTop={10} overflowX="clip">
      <Flex
        width={`${images.length * 100}%`}
        flexDirection="row"
        transform={`translateX(-${(currentImageIndex / images.length) * 100}%)`}
        transition="transform .5s"
      >
        {images.map((image) => (
          <Box flex={1} key={image} alignItems="center" display="flex">
            <Image src={image} w="100%" maxH="50rem" objectFit="contain" objectPosition="center" />
          </Box>
        ))}
      </Flex>
      <Flex paddingTop={5} alignItems="center" justify="space-between">
        <DirectionButton direction={Directions.LEFT} onClick={previousImage} />
        <ButtonGroup display="flex" alignItems="center">
          {images.map((_image, index) => (
            <CurrentImageIndicatorDot key={index} index={index} currentIndex={currentImageIndex} onClick={setCurrentImageIndex} />
          ))}
        </ButtonGroup>
        <DirectionButton direction={Directions.RIGHT} onClick={nextImage} />
      </Flex>
    </Flex>
  )
}

type CurrentImageIndicatorDotProps = {
  index: number
  currentIndex: number
  onClick: (index: number) => void
}

const CurrentImageIndicatorDot = ({ index, currentIndex, onClick }: CurrentImageIndicatorDotProps) => {
  const brandColor = useBrandColor(500, 500)
  return (
    <Button
      height="10px"
      width="10px"
      padding={0}
      borderWidth={2}
      borderStyle="solid"
      borderColor={brandColor}
      borderRadius="full"
      cursor="pointer"
      transition="border-width .1s"
      _hover={{ borderWidth: 10 }}
      backgroundColor={index === currentIndex ? brandColor : 'transparent'}
      onClick={() => {
        onClick(index)
      }}
    />
  )
}

type DirectionButtonProps = {
  direction: Directions
  onClick: () => void
}

const Directions = {
  LEFT: 'left',
  RIGHT: 'right'
}
type Directions = (typeof Directions)[keyof typeof Directions]

const DirectionButton = ({ direction, onClick }: DirectionButtonProps) => (
  <IconButton
    icon={direction === Directions.LEFT ? <ChevronLeftIcon /> : <ChevronRightIcon />}
    colorScheme={useBrandColor()}
    onClick={onClick}
    padding={0}
    variant="ghost"
    aria-label={direction === Directions.LEFT ? 'Előző kép' : 'következő kép'}
  />
)
