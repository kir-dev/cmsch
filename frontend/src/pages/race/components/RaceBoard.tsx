import { Divider, Flex, Heading, Input, InputGroup, InputLeftElement } from '@chakra-ui/react'
import { Helmet } from 'react-helmet-async'
import { Race } from '../../../api/contexts/config/types'
import { BoardStat } from '../../../common-components/BoardStat'
import { ComponentUnavailable } from '../../../common-components/ComponentUnavailable'
import { CmschPage } from '../../../common-components/layout/CmschPage'
import { LeaderBoardTable } from '../../../common-components/LeaderboardTable'
import Markdown from '../../../common-components/Markdown'
import { PageStatus } from '../../../common-components/PageStatus'
import { RaceView } from '../../../util/views/race.view'
import { createRef, useEffect, useState } from 'react'
import { LeaderBoardItemView } from '../../../util/views/leaderBoardView'
import { SearchIcon } from '@chakra-ui/icons'

type Props = {
  data: RaceView | undefined
  component: Race
  isError: boolean
  isLoading: boolean
}

const RaceBoard = ({ data, component, isError, isLoading }: Props) => {
  const inputRef = createRef<HTMLInputElement>()
  const [filteredBoard, setFilteredBoard] = useState<LeaderBoardItemView[] | undefined>(data?.board)

  const handleInput = () => {
    const search = inputRef?.current?.value.toLowerCase()
    if (!data) {
      setFilteredBoard(undefined)
    } else if (!search) setFilteredBoard(data?.board)
    else {
      setFilteredBoard(
        data.board?.filter((item) => {
          return item.name.toLocaleLowerCase().includes(search)
        })
      )
    }
  }
  useEffect(() => {
    if (data) {
      setFilteredBoard(data?.board)
      if (inputRef.current) inputRef.current.value = ''
    }
  }, [data])
  if (!component || !component.visible) return <ComponentUnavailable />
  if (isError || isLoading || !data) return <PageStatus isLoading={isLoading} isError={isError} title={component.title} />

  return (
    <CmschPage>
      <Helmet title={data.categoryName} />
      <Heading mb={3}>{data.categoryName}</Heading>
      {component.searchEnabled && (
        <InputGroup mt={5}>
          <InputLeftElement h="100%">
            <SearchIcon />
          </InputLeftElement>
          <Input ref={inputRef} placeholder="Keresés..." size="lg" onChange={handleInput} autoFocus={true} />
        </InputGroup>
      )}
      <Markdown text={data.description || component.defaultCategoryDescription} />
      <Flex my={5} gap={5} flexWrap="wrap">
        <BoardStat label="Helyezésed" value={data.place || '-'} />
        <BoardStat label="Legjobb időd" value={(data.bestTime || '-') + ' mp'} />
      </Flex>
      <Divider mb={10} />
      <LeaderBoardTable data={filteredBoard || []} showGroup={true} suffix="mp" />
    </CmschPage>
  )
}
export default RaceBoard
