import {useQuery} from "react-query";
import {TournamentDetailsView} from "../../../util/views/tournament.view.ts";
import {QueryKeys} from "../queryKeys.ts";
import axios from "axios";
import {joinPath} from "../../../util/core-functions.util.ts";
import {ApiPaths} from "../../../util/paths.ts";


export const useTournamentQuery = (id: number, onError?: (err: any) => void) => {
  return useQuery<TournamentDetailsView, Error>(
    [QueryKeys.TOURNAMENTS, id],
    async () => {
      const response = await axios.get<TournamentDetailsView>(joinPath(ApiPaths.TOURNAMENTS, id))
      return response.data
    },
    { onError: onError }
  )

}
