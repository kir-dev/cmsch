import axios from 'axios'
import { useState } from 'react'
import { ApiPaths } from '../../../util/paths.ts'
import { type SendAnswerDto, SendAnswerResponseStatus } from '../../../util/views/tinder.ts'

export const useTinderAnswerSend = () => {
  const [data, setData] = useState<SendAnswerResponseStatus>()
  const submit = (answers: SendAnswerDto, edit?: boolean) => {
    if (edit) {
      axios
        .put<SendAnswerResponseStatus>(ApiPaths.TINDER_ANSWERS, answers)
        .then((res) => setData(res.data))
        .catch(console.error)
    } else {
      axios
        .post<SendAnswerResponseStatus>(ApiPaths.TINDER_ANSWERS, answers)
        .then((res) => setData(res.data))
        .catch(console.error)
    }
  }
  return { response: data, submit }
}
